import { Component, OnInit } from '@angular/core';
import {BrowserDescriptor, Descriptor} from "../../../platform/model/descriptor";
import {ActivatedRoute, Router} from "@angular/router";
import {environment} from "../../../environments/environment";
import {config} from "rxjs";

@Component({
  selector: 'common-browser',
  templateUrl: './common-browser.component.html',
  styleUrls: ['./common-browser.component.scss']
})
export class CommonBrowserComponent {

  loading: boolean = true
  descriptor!: BrowserDescriptor

  private config: any = {
    'account': 'account.entity',
    'expense-item': 'expense.item.entity',
    'income-item': 'income.item.entity',
    'person': 'person.entity',
    'ticker-symbol': 'ticker.symbol.entity.entity',
    'document': 'document.entity'
  }

  constructor(private activatedRoute: ActivatedRoute) {
    this.activatedRoute.params
      .subscribe(params => {
        let entity = params['entity']
        let localizationKey = this.config[entity]
        this.descriptor = new DescriptorImpl(entity, localizationKey)
        this.loading = false
      })
  }

}

class DescriptorImpl implements BrowserDescriptor {
  private readonly entity: string
  private readonly _localizationKey: string

  constructor(entity: string, localizationKey: string) {
    this.entity = entity
    this._localizationKey = localizationKey;
  }

  dtoModel(): string {return `${environment.api}/reference/${this.entity}/descriptor/dto`}
  metModel(): string {return `${environment.api}/reference/${this.entity}/descriptor/meta`}
  count(): string {return `${environment.api}/reference/${this.entity}/count`}
  create(): string {return `/common/${this.entity}/new`}
  read(): string {return `${environment.api}/reference/${this.entity}/`}
  update(entity: any): string {return `/common/${this.entity}/${entity.id}`}
  delete(id: number): string {return `${environment.api}/reference/${this.entity}/${id}`}
  localizationKey(field: string): string {return `${this._localizationKey}.${field}`}
}
