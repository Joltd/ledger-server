import {Type} from "class-transformer";

export class Descriptor {
  @Type(() => DtoModel)
  dto!: DtoModel
  @Type(() => MetaModel)
  meta!: MetaModel
}

export class DtoModel {
  @Type(() => DtoField)
  fields: DtoField[] = []
}

export class DtoField {
  reference: string = ''
  sort: boolean = false
  format: string = ''
}

export class MetaModel {
  @Type(() => MetaField)
  fields: MetaField[] = []
}

export class MetaField {
  reference: string = ''
  type!: MetaType
  @Type(() => MetaField)
  fields: MetaField[] = []
}

export enum MetaType {
  STRING,
  NUMBER,
  DATE,
  BOOlEAN,
  OBJECT
}
