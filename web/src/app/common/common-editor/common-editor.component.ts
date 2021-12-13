import { Component, OnInit } from '@angular/core';
import {Descriptor, EditorDescriptor} from "../../../platform/model/descriptor";
import {environment} from "../../../environments/environment";
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-common-editor',
  templateUrl: './common-editor.component.html',
  styleUrls: ['./common-editor.component.scss']
})
export class CommonEditorComponent {

  descriptor!: EditorDescriptor

  private config: any = {
    'account': 'account.entity',
    'expense-item': 'expense.item.entity',
    'income-item': 'income.item.entity',
    'person': 'person.entity',
    'ticker-symbol': 'ticker.symbol.entity.entity'
  }

  constructor(private activatedRoute: ActivatedRoute) {
    this.activatedRoute.params
      .subscribe(params => {
        let entity = params['entity']
        let localizationKey = this.config[entity]
        this.descriptor = new DescriptorImpl(entity, localizationKey)
      })
  }

}

class DescriptorImpl implements EditorDescriptor {

  private readonly entity: string
  private readonly _localizationKey: string

  constructor(entity: string, localizationKey: string) {
    this.entity = entity;
    this._localizationKey = localizationKey;
  }

  metaModel(): string {return `${environment.api}/reference/${this.entity}/descriptor/meta`}
  read(id: number): string {return `${environment.api}/reference/${this.entity}/${id}`}
  update(): string {return `${environment.api}/reference/${this.entity}`}
  backTo(): string {return `/common/${this.entity}`}
  localizationKey(field: string): string {return `${this._localizationKey}.${field}`}

}
