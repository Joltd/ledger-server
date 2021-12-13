import { Component, OnInit } from '@angular/core';
import {BrowserDescriptor} from "../../../platform/model/descriptor";
import {ActivatedRoute} from "@angular/router";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'document-browser',
  templateUrl: './document-browser.component.html',
  styleUrls: ['./document-browser.component.scss']
})
export class DocumentBrowserComponent {

  descriptor: BrowserDescriptor = new DescriptorImpl()

}

class DescriptorImpl implements BrowserDescriptor {
  dtoModel(): string {return `${environment.api}/document/browser/descriptor/dto`}
  metModel(): string {return `${environment.api}/document/browser/descriptor/meta`}
  count(): string {return `${environment.api}/document/browser/count`}
  create(): string {return `/document/new`}
  read(): string {return `${environment.api}/document/browser/`}
  update(entity: any): string {return `/document/${DescriptorImpl.snakeCaseToHyphen(entity.type)}/${entity.id}`}
  delete(id: number): string {return `${environment.api}/document/browser/${id}`}
  localizationKey(field: string): string {return `document.entity.${field}`}

  private static snakeCaseToHyphen(value: string) {
    return value.replace('_', '-').toLowerCase()
  }
}
