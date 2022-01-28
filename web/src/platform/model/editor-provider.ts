import {Observable} from "rxjs";
import {FieldType} from "./field-type";

export interface EditorProvider {

  entityModel(): EntityField[]

  byId(): Observable<any>

  update(entity: any): Observable<void>

  done(): void

}

export class EntityField {
  name: string = ''
  type!: FieldType
  localization: string = ''
}

