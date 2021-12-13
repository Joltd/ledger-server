import {Type} from "class-transformer";

export class Descriptor {
  readonly localizationKey: string
  readonly backend: string
  readonly frontend: string

  constructor(localizationKey: string, backend: string, frontend: string) {
    this.localizationKey = localizationKey;
    this.backend = backend;
    this.frontend = frontend;
  }

  fieldId(field: string) {
    return this.localizationKey + '.' + field
  }
}

export interface BrowserDescriptor {
  dtoModel(): string
  metModel(): string
  count(): string
  create(): string
  read(): string
  update(entity: any): string
  delete(id: number): string
  localizationKey(field: string): string
}

export interface EditorDescriptor {
  metaModel(): string
  read(id: number): string
  update(): string
  backTo(): string
  localizationKey(field: string): string
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
  localizationKey: string = ''
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
  api!: string
  localizationKey: string = ''
}

export type FieldType = 'STRING' | 'NUMBER' | 'DATE' | 'BOOLEAN' | 'OBJECT' | 'ENUM'
