import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {restInterceptorProvider} from "./service/rest-interceptor.service";
import {PlatformModule} from "../platform/platform.module";
import {HttpClientModule} from '@angular/common/http';
import {TranslocoRootModule} from './transloco-root.module';
import {MatCardModule} from "@angular/material/card";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatSidenavModule} from "@angular/material/sidenav";
import {OrderBrowserComponent} from './reference/order/component/order-browser/order-browser.component';
import {MatListModule} from "@angular/material/list";
import {MatMenuModule} from "@angular/material/menu";
import {OrderEditorComponent} from './reference/order/component/order-editor/order-editor.component';
import {AccountBrowserComponent} from './reference/account-browser/account-browser.component';
import {AccountEditorComponent} from './reference/account-editor/account-editor.component';
import {ExpenseItemBrowserComponent} from "./reference/expense-item-browser/expense-item-browser.component";
import {ExpenseItemEditorComponent} from "./reference/expense-item-editor/expense-item-editor.component";
import { IncomeItemEditorComponent } from './reference/income-item-editor/income-item-editor.component';
import { IncomeItemBrowserComponent } from './reference/income-item-browser/income-item-browser.component';
import { PersonBrowserComponent } from './reference/person-browser/person-browser.component';
import { PersonEditorComponent } from './reference/person-editor/person-editor.component';
import { TickerSymbolEditorComponent } from './reference/ticker-symbol-editor/ticker-symbol-editor.component';
import { TickerSymbolBrowserComponent } from './reference/ticker-symbol-browser/ticker-symbol-browser.component';

@NgModule({
  declarations: [
    AppComponent,
    OrderBrowserComponent,
    OrderEditorComponent,
    AccountBrowserComponent,
    AccountEditorComponent,
    ExpenseItemBrowserComponent,
    ExpenseItemEditorComponent,
    IncomeItemEditorComponent,
    IncomeItemBrowserComponent,
    PersonBrowserComponent,
    PersonEditorComponent,
    TickerSymbolEditorComponent,
    TickerSymbolBrowserComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    PlatformModule,
    HttpClientModule,
    TranslocoRootModule,
    MatCardModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatSidenavModule,
    MatListModule,
    MatMenuModule
  ],
  providers: [
    restInterceptorProvider
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
