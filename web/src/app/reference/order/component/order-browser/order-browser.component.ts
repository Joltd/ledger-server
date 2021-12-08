import {Component, OnInit} from '@angular/core';
import {Reference} from "../../../../../platform/model/reference";
import {environment} from "../../../../../environments/environment";
import {Descriptor} from "../../../../../platform/model/descriptor";

@Component({
  selector: 'order-browser',
  templateUrl: './order-browser.component.html',
  styleUrls: ['./order-browser.component.scss']
})
export class OrderBrowserComponent implements OnInit {

  descriptor: Descriptor = new Descriptor('order.browser', environment.api + '/order', '/order')

  constructor() {}

  ngOnInit(): void {}

}
