import { Component, OnInit } from '@angular/core';
import {Descriptor} from "../../../platform/model/descriptor";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'income-item-editor',
  templateUrl: './income-item-editor.component.html',
  styleUrls: ['./income-item-editor.component.scss']
})
export class IncomeItemEditorComponent implements OnInit {

  descriptor: Descriptor = new Descriptor('income.item.entity', environment.api + '/reference/income-item', '/income-item')

  constructor() {}

  ngOnInit(): void {}

}
