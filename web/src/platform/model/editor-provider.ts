import {Observable} from "rxjs";
import {FieldType} from "./field-type";

export interface EditorProvider {

  entityModel(): EntityField[]

  byId(): Observable<any>

  update(entity: any): Observable<void>

  done(): void

  endpoint(entityName: string): string

}

export class EntityField {
  name: string = ''
  typeName!: string
  type!: FieldType
  localization: string = ''
}

