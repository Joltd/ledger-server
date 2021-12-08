import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {OrderBrowserComponent} from "./reference/order/component/order-browser/order-browser.component";
import {OrderEditorComponent} from "./reference/order/component/order-editor/order-editor.component";
import {AccountBrowserComponent} from "./reference/account-browser/account-browser.component";
import {AccountEditorComponent} from "./reference/account-editor/account-editor.component";
import {ExpenseItemBrowserComponent} from "./reference/expense-item-browser/expense-item-browser.component";
import {ExpenseItemEditorComponent} from "./reference/expense-item-editor/expense-item-editor.component";
import {IncomeItemBrowserComponent} from "./reference/income-item-browser/income-item-browser.component";
import {IncomeItemEditorComponent} from "./reference/income-item-editor/income-item-editor.component";
import {PersonBrowserComponent} from "./reference/person-browser/person-browser.component";
import {PersonEditorComponent} from "./reference/person-editor/person-editor.component";
import {TickerSymbolBrowserComponent} from "./reference/ticker-symbol-browser/ticker-symbol-browser.component";
import {TickerSymbolEditorComponent} from "./reference/ticker-symbol-editor/ticker-symbol-editor.component";

const routes: Routes = [
  { path: 'account', component: AccountBrowserComponent },
  { path: 'account/:id', component: AccountEditorComponent },
  { path: 'expense-item', component: ExpenseItemBrowserComponent },
  { path: 'expense-item/:id', component: ExpenseItemEditorComponent },
  { path: 'income-item', component: IncomeItemBrowserComponent },
  { path: 'income-item/:id', component: IncomeItemEditorComponent },
  { path: 'person', component: PersonBrowserComponent },
  { path: 'person/:id', component: PersonEditorComponent },
  { path: 'ticker-symbol', component: TickerSymbolBrowserComponent },
  { path: 'ticker-symbol/:id', component: TickerSymbolEditorComponent },

  { path: 'order', component: OrderBrowserComponent },
  { path: 'order/:id', component: OrderEditorComponent },

  { path: '', redirectTo: '/order', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
