import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {restInterceptorProvider} from "./service/rest-interceptor.service";
import {PlatformModule} from "../platform/platform.module";
import { HttpClientModule } from '@angular/common/http';
import { TranslocoRootModule } from './transloco-root.module';
import {MatCardModule} from "@angular/material/card";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatSidenavModule} from "@angular/material/sidenav";
import { OrderBrowserComponent } from './reference/order/component/order-browser/order-browser.component';
import {MatListModule} from "@angular/material/list";
import {MatMenuModule} from "@angular/material/menu";
import { OrderEditorComponent } from './reference/order/component/order-editor/order-editor.component';

@NgModule({
  declarations: [
    AppComponent,
    OrderBrowserComponent,
    OrderEditorComponent
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
