import {Observable} from "rxjs";
import {LoadConfig} from "./load-config";
import {FieldType} from "./field-type";

export interface BrowserProvider {
  filterModel(): FilterField[]

  rowModel(): RowField[]

  count(config: LoadConfig): Observable<number>

  load(config: LoadConfig): Observable<any[]>

  delete(id: number): Observable<void>

  add(): void

  edit(entity: any): void
}

export class FilterField {
  name: string = ''
  type!: FieldType
  endpoint!: string
  localization: string = ''

  constructor(name: string, type: FieldType, endpoint: string, localization: string) {
    this.name = name;
    this.type = type;
    this.endpoint = endpoint;
    this.localization = localization;
  }
}

export class RowField {
  name: string = ''
  type!: FieldType
  sort: boolean = false
  format!: string
  localization: string = ''
}
