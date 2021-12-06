import { Component, OnInit } from '@angular/core';
import {Reference} from "../../../../../platform/model/reference";
import {environment} from "../../../../../environments/environment";
import {OverlayService} from "../../../../../platform/service/overlay.service";

@Component({
  selector: 'order-browser',
  templateUrl: './order-browser.component.html',
  styleUrls: ['./order-browser.component.scss']
})
export class OrderBrowserComponent implements OnInit {

  reference: Reference = new Reference('order.browser', environment.api + '/order')

  constructor() {}

  ngOnInit(): void {}

}
