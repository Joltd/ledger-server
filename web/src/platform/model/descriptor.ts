import {Type} from "class-transformer";

export class Descriptor {
  readonly id: string
  readonly backend: string
  readonly frontend: string

  constructor(id: string, backend: string, frontend: string) {
    this.id = id;
    this.backend = backend;
    this.frontend = frontend;
  }

  fieldId(field: string) {
    return this.id + '.' + field
  }
}

export class DtoModel {
  @Type(() => DtoField)
  fields: DtoField[] = []
}

export class DtoField {
  reference: string = ''
  sort: boolean = false
  type!: FieldType
  format: string = ''
}

export class MetaModel {
  @Type(() => MetaField)
  fields: MetaField[] = []
}

export class MetaField {
  reference: string = ''
  type!: FieldType
  @Type(() => MetaField)
  fields: MetaField[] = []
}

export type FieldType = 'STRING' | 'NUMBER' | 'DATE' | 'BOOLEAN' | 'OBJECT'
