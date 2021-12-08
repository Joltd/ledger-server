import {Component, OnInit} from '@angular/core';
import {Reference} from "../../../platform/model/reference";
import {environment} from "../../../environments/environment";
import {Descriptor} from "../../../platform/model/descriptor";

@Component({
  selector: 'account-browser',
  templateUrl: './expense-item-browser.component.html',
  styleUrls: ['./expense-item-browser.component.scss']
})
export class ExpenseItemBrowserComponent implements OnInit {

  descriptor: Descriptor = new Descriptor('expense.item.entity', environment.api + '/reference/expense-item', '/expense-item')

  constructor() {}

  ngOnInit(): void {}

}
