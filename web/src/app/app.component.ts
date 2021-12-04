import { Component } from '@angular/core';
import {environment} from "../environments/environment";
import {Reference} from "../platform/model/reference";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  reference: Reference = new Reference()

  constructor() {
    this.reference.api = environment.api + '/order'
    this.reference.id = 'order.browser'
  }
}
