import { Component, OnInit } from '@angular/core';
import {Reference} from "../../../../../platform/model/reference";
import {environment} from "../../../../../environments/environment";

@Component({
  selector: 'order-editor',
  templateUrl: './order-editor.component.html',
  styleUrls: ['./order-editor.component.scss']
})
export class OrderEditorComponent implements OnInit {

  reference: Reference = new Reference('order.browser', environment.api + '/order', '/order/')

  constructor() {}

  ngOnInit(): void {}

}
