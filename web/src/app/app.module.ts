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

@NgModule({
  declarations: [
    AppComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        PlatformModule,
        HttpClientModule,
        TranslocoRootModule,
        MatCardModule
    ],
  providers: [
    restInterceptorProvider
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
