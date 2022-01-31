import { Injectable } from '@angular/core';
import {BrowserProvider, FilterField, RowField} from "../../platform/model/browser-provider";
import {Observable, of} from "rxjs";
import {LoadConfig} from "../../platform/model/load-config";
import {EditorProvider, EntityField} from "../../platform/model/editor-provider";
import {Router, UrlMatchResult, UrlSegment} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {Type} from "class-transformer";
import {environment} from "../../environments/environment";
import {TypeUtils} from "../../core/type-utils";
import {FieldType} from "../../platform/model/field-type";
import {map} from "rxjs/operators";
import {EntityType} from "../model/entity-type";

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
          throw `Unknown entity [${entityName}]`
        }
        this.entity = entity
        this.id = id
      })
  }

  hasEntity(): boolean {
    return this.entity != undefined
  }

  isReference(): boolean {
    return this.id != undefined && this.entity.entityType == 'REFERENCE'
  }

  isDocument(): boolean {
    return this.id != undefined && this.entity.entityType == 'DOCUMENT'
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
    let result = [];
    for (let field of this.entity.fields) {

      let entity = this.findEntity(field.typeName)
      let localization = this.entity.localization + '.' + field.name;

      if (field.type == 'OBJECT') {

        let idField = new FilterField(
          field.name + 'Id',
          'NUMBER',
          this.endpoint(field.typeName),
          entity.localization + '.id'
        );
        result.push(idField);

        let nameField = new FilterField(
          field.name + 'Name',
          'STRING',
          this.endpoint(field.typeName),
          entity.localization + '.name'
        );
        result.push(nameField)

      } else {

        let filterField = new FilterField(field.name, field.type, this.endpoint(field.typeName), localization);
        result.push(filterField);

      }

    }
    return result
  }

  rowModel(): RowField[] {
    return this.entity.fields
      .map(field => {
        let rowField = new RowField()
        rowField.name = field.name
        rowField.type = field.type
        rowField.localization = this.entity.localization + '.' + field.name
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
        entityField.typeName = field.typeName
        entityField.localization = this.entity.localization + '.' + field.name
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

  endpoint(entityName: string): string {
    if (!entityName) {
      return ''
    }

    return this.findEntity(entityName).endpoint
  }

  private findEntity(name: string) {
    let entity = this.entities.find(entity => entity.name == name)
    if (!entity) {
      throw `Unknown entity [${name}]`
    }
    return entity
  }

}

export class MetaEntity {
  name: string = ''
  entityType: EntityType = 'REFERENCE'
  key: string = ''
  localization: string = ''
  endpoint: string = ''
  @Type(() => MetaEntityField)
  fields: MetaEntityField[] = []
}

export class MetaEntityField {
  name: string = ''
  type!: FieldType
  typeName!: string
  sort: boolean = false
  format: string = ''
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
