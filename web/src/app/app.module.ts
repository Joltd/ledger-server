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
import {MatListModule} from "@angular/material/list";
import {MatMenuModule} from "@angular/material/menu";
import {CommonBrowserComponent} from "./common/common-browser/common-browser.component";
import { CommonEditorComponent } from './common/common-editor/common-editor.component';
import { DocumentEditorComponent } from './common/document-editor/document-editor.component';
import { DocumentBrowserComponent } from './common/document-browser/document-browser.component';

@NgModule({
  declarations: [
    AppComponent,
    CommonBrowserComponent,
    CommonEditorComponent,
    DocumentEditorComponent,
    DocumentBrowserComponent
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
