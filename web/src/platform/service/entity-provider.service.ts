import { Injectable } from '@angular/core';
import {BrowserProvider, FilterField, RowField} from "../model/browser-provider";
import {Observable} from "rxjs";
import {LoadConfig} from "../model/load-config";
import {EditorProvider, EntityField} from "../model/editor-provider";
import {UrlMatchResult, UrlSegment} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {Type} from "class-transformer";
import {environment} from "../../environments/environment";
import {TypeUtils} from "../../core/type-utils";
import {FieldType} from "../model/field-type";

@Injectable({
  providedIn: 'root'
})
export class EntityProviderService implements BrowserProvider,EditorProvider {

  entities: MetaEntity[] = []

  private entity!: string
  private id?: number

  constructor(private http: HttpClient) {}

  loadMeta() {
    this.http.get<MetaEntity[]>(environment.api + '/meta', TypeUtils.of(MetaEntity))
      .subscribe(result => this.entities = result)
  }

  setupEntity(entity: string, id: number | undefined) {
    this.entity = entity
    this.id = id
  }

  hasEntity(): boolean {
    return this.entity != undefined
  }

  hasId(): boolean {
    return this.id != undefined
  }

  private metaEntity(): MetaEntity {
    let metaEntity = this.entities.find(entity => entity.fullName == this.entity)
    if (!metaEntity) {
      throw `Unknown entity [${this.entity}]`
    }
    return metaEntity
  }

  filterModel(): FilterField[] {
    let metaEntity = this.metaEntity()
    return metaEntity.fields
      .map(field => {
        let filterField = new FilterField();
        filterField.name = field.name
        filterField.type = field.type
        filterField.endpoint = ''
        filterField.localization = field.localization
        return filterField
      })
  }

  rowModel(): RowField[] {
    let metaEntity = this.metaEntity();
    return metaEntity.fields
      .map(field => {
        let rowField = new RowField()
        rowField.name = field.name
        rowField.type = field.type
        rowField.localization = field.localization
        rowField.sort = field.sort
        rowField.format = field.format
        return rowField
      })
  }

  count(config: LoadConfig): Observable<number> {
    return new Observable<number>()
  }

  load(config: LoadConfig): Observable<any[]> {
    return new Observable<any[]>()
  }

  delete(id: number): Observable<void> {
    return new Observable<void>()
  }

  add(): void {

  }

  edit(entity: any): void {

  }

  entityModel(): EntityField[] {
    let metaEntity = this.metaEntity();
    return metaEntity.fields
      .map(field => {
        let entityField = new EntityField();
        entityField.name = field.name
        entityField.type = field.type
        entityField.localization = field.localization
        return entityField
      })
  }

  loadById(): Observable<any> {
    return new Observable<any>()
  }

  update(entity: any): Observable<void> {
    return new Observable<void>()
  }

  done(): void {

  }

}

export class MetaEntity {
  name: string = ''
  fullName: string = ''
  localization: string = ''
  @Type(() => MetaEntityField)
  fields: MetaEntityField[] = []
}

export class MetaEntityField {
  name: string = ''
  type!: FieldType
  sort: boolean = false
  format: string = ''
  localization: string = ''
}

export function entityBrowserMatcher(segments: UrlSegment[]): UrlMatchResult | null {
  if (segments.length < 1) {
    return null
  }

  if (segments[0].path != 'entity') {
    return null
  }

  let last = segments[segments.length - 1].path;
  if (last == 'new') {
    return null
  }

  let id = parseInt(last);
  if (!isNaN(id)) {
    return null
  }

  return {
    consumed: segments,
    posParams: {
      entity: new UrlSegment(segments.slice(1).join("/"), {})
    }
  }
}

export function entityEditorMatcher(segments: UrlSegment[]): UrlMatchResult | null {
  if (segments.length < 1) {
    return null
  }

  if (segments[0].path != 'entity') {
    return null
  }

  let last = segments[segments.length - 1].path;
  if (last == 'new') {
    return {
      consumed: segments,
      posParams: {
        entity: new UrlSegment(segments.slice(1, segments.length - 1).join("/"), {})
      }
    }
  }

  let id = parseInt(last);
  if (isNaN(id)) {
    return null
  }

  return {
    consumed: segments,
    posParams: {
      entity: new UrlSegment(segments.slice(1, segments.length - 1).join("/"), {}),
      id: new UrlSegment(id.toString(), {})
    }
  }
}
