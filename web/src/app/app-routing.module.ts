import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {CommonBrowserComponent} from "./common/common-browser/common-browser.component";
import {CommonEditorComponent} from "./common/common-editor/common-editor.component";
import {DocumentEditorComponent} from "./common/document-editor/document-editor.component";
import {DocumentBrowserComponent} from "./common/document-browser/document-browser.component";

const routes: Routes = [
  { path: 'common/:entity', component: CommonBrowserComponent },
  { path: 'common/:entity/:id', component: CommonEditorComponent },
  { path: 'document', component: DocumentBrowserComponent },
  // { path: '/common/document/new', component: null },
  { path: 'document/:type/:id', component: DocumentEditorComponent },
  { path: '', redirectTo: '/common/account', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
