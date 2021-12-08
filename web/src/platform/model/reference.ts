import {MetaField} from "./descriptor";

export class Reference {
  id: string
  api: string
  editor: string

  constructor(id: string, api: string, editor: string) {
    this.id = id;
    this.api = api;
    this.editor = editor
  }

  fieldId(field: string) {
    return this.id + '.' + field
  }

}
