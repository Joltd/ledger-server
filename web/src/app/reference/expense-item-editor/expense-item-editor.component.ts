import {Component, OnInit} from '@angular/core';
import {Reference} from "../../../platform/model/reference";
import {environment} from "../../../environments/environment";
import {Descriptor} from "../../../platform/model/descriptor";

@Component({
  selector: 'expense-item-editor',
  templateUrl: './expense-item-editor.component.html',
  styleUrls: ['./expense-item-editor.component.scss']
})
export class ExpenseItemEditorComponent implements OnInit {

  descriptor: Descriptor = new Descriptor('expense.item.entity', environment.api + '/reference/expense-item', '/expense-item')

  constructor() {}

  ngOnInit(): void {}

}
