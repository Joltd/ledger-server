import { Injectable } from '@angular/core';
import {BrowserProvider, FilterField, RowField} from "../model/browser-provider";
import {BehaviorSubject, forkJoin, from, Observable, of, Subject} from "rxjs";
import {LoadConfig} from "../model/load-config";
import {EditorProvider, EntityField} from "../model/editor-provider";
import {Router, UrlMatchResult, UrlSegment} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {Type} from "class-transformer";
import {environment} from "../../environments/environment";
import {TypeUtils} from "../../core/type-utils";
import {FieldType} from "../model/field-type";
import {map} from "rxjs/operators";

@Injectable({
  providedIn: 'root'
})
export class EntityProviderService implements BrowserProvider,EditorProvider {

  entities: MetaEntity[] = []
  private entity!: MetaEntity
  private id?: string

  constructor(
    private http: HttpClient,
    private router: Router
  ) {}

  setupEntity(entityName: string, id: string | undefined) {
    this.loadMeta()
      .subscribe(() => {
        let entity = this.entities.find(entity => entity.key == entityName)
        if (!entity) {
          throw `Unknown entity [${entity}]`
        }
        this.entity = entity
        this.id = id
      })
  }

  hasEntity(): boolean {
    return this.entity != undefined
  }

  hasId(): boolean {
    return this.id != undefined
  }

  loadMeta(): Observable<MetaEntity[]> {
    if (this.entities.length > 0) {
      return of(this.entities).pipe(map(entities => {
        return entities
      }))
    } else {
      return this.http.get<MetaEntity[]>(environment.api + '/meta', TypeUtils.of(MetaEntity))
        .pipe(map(result => this.entities = result))
    }
  }

  filterModel(): FilterField[] {
    return this.entity.fields
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
    return this.entity.fields
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
    return this.http.post<number>(environment.api + this.entity.endpoint + '/count', config)
  }

  load(config: LoadConfig): Observable<any[]> {
    return this.http.post<any[]>(environment.api + this.entity.endpoint + '/', config)
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(environment.api + this.entity.endpoint + '/' + id)
  }

  add(): void {
    this.router.navigate(['entity', this.entity.key, 'new'])
      .then()
  }

  edit(entity: any): void {
    this.router.navigate(['entity', this.entity.key, entity.id])
      .then()
  }

  entityModel(): EntityField[] {
    return this.entity.fields
      .map(field => {
        let entityField = new EntityField();
        entityField.name = field.name
        entityField.type = field.type
        entityField.localization = field.localization
        return entityField
      })
  }

  byId(): Observable<any> {
    if (this.id == 'new') {
      return of({})
    } else {
      return this.http.get(environment.api + this.entity.endpoint + '/' + this.id)
    }
  }

  update(entity: any): Observable<void> {
    return this.http.post<void>(environment.api + this.entity.endpoint, entity)
  }

  done(): void {
    this.router.navigate(['entity', this.entity.key])
      .then()
  }

}

export class MetaEntity {
  name: string = ''
  key: string = ''
  localization: string = ''
  endpoint: string = ''
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

  let entity = segments.slice(1).join("/")
  if (entity.length == 0) {
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
      entity: new UrlSegment(entity, {})
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

  let entity = segments.slice(1, segments.length - 1).join("/")
  if (entity.length == 0) {
    return null
  }

  let last = segments[segments.length - 1].path;
  if (last == 'new') {
    return {
      consumed: segments,
      posParams: {
        entity: new UrlSegment(entity, {}),
        id: new UrlSegment(last, {})
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
      entity: new UrlSegment(entity, {}),
      id: new UrlSegment(id.toString(), {})
    }
  }
}
