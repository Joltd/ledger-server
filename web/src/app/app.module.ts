import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {restInterceptorProvider} from "./service/rest-interceptor.service";
import {EntityBrowserComponent} from "./component/entity-browser/entity-browser.component";
import {EntityEditorComponent} from "./component/entity-editor/entity-editor.component";
import {EntityListComponent} from "./component/entity-list/entity-list.component";
import {DocumentEditorComponent} from "./component/document-editor/document-editor.component";
import {EntityProviderService} from "./service/entity-provider.service";
import {MatCardModule} from "@angular/material/card";
import {ReactiveFormsModule} from "@angular/forms";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";
import {PlatformModule} from "../platform/platform.module";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {TranslocoRootModule} from "../platform/transloco-root.module";
import {CommonModule} from "@angular/common";
import {MatListModule} from "@angular/material/list";
import {RouterModule} from "@angular/router";
import {MatToolbarModule} from "@angular/material/toolbar";
import {MatButtonModule} from "@angular/material/button";
import {MatIconModule} from "@angular/material/icon";
import {MatMenuModule} from "@angular/material/menu";
import {MatSidenavModule} from "@angular/material/sidenav";

@NgModule({
  declarations: [
    AppComponent,
    EntityBrowserComponent,
    EntityEditorComponent,
    EntityListComponent,
    DocumentEditorComponent
  ],
  imports: [
    MatCardModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    PlatformModule,
    MatDatepickerModule,
    MatSlideToggleModule,
    TranslocoRootModule,
    CommonModule,
    MatListModule,
    RouterModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatMenuModule,
    MatSidenavModule

  ],
  providers: [
    restInterceptorProvider,
    EntityProviderService
  ],
  bootstrap: [AppComponent]
})
export class AppModule {}
