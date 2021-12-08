import { Component, OnInit } from '@angular/core';
import {Descriptor} from "../../../platform/model/descriptor";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'income-item-browser',
  templateUrl: './income-item-browser.component.html',
  styleUrls: ['./income-item-browser.component.scss']
})
export class IncomeItemBrowserComponent implements OnInit {

  descriptor: Descriptor = new Descriptor('income.item.entity', environment.api + '/reference/income-item', '/income-item')

  constructor() { }

  ngOnInit(): void {}

}
