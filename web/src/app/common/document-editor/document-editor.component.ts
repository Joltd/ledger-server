import { Component, OnInit } from '@angular/core';
import {Descriptor, EditorDescriptor} from "../../../platform/model/descriptor";
import {ActivatedRoute} from "@angular/router";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'document-editor',
  templateUrl: './document-editor.component.html',
  styleUrls: ['./document-editor.component.scss']
})
export class DocumentEditorComponent {

  descriptor!: EditorDescriptor

  constructor(private activatedRoute: ActivatedRoute) {
    this.activatedRoute.params
      .subscribe(params => {
        let type = params['type']
        this.descriptor = new DescriptorImpl(type)
      })
  }

}

class DescriptorImpl implements EditorDescriptor {

  private readonly type: string

  constructor(type: string) {
    this.type = type;
  }

  metaModel(): string {return `${environment.api}/document/editor/${this.type}/descriptor/meta`}
  read(id: number): string {return `${environment.api}/document/editor/${id}`}
  update(): string {return `${environment.api}/document/editor`}
  backTo(): string {return `/document`}
  localizationKey(field: string): string {return `document.entity.${field}`}

}
