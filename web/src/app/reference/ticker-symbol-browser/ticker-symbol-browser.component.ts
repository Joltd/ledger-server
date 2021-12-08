import { Component, OnInit } from '@angular/core';
import {Descriptor} from "../../../platform/model/descriptor";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'ticker-symbol-browser',
  templateUrl: './ticker-symbol-browser.component.html',
  styleUrls: ['./ticker-symbol-browser.component.scss']
})
export class TickerSymbolBrowserComponent implements OnInit {

  descriptor: Descriptor = new Descriptor('ticker.symbol.entity', environment.api + '/reference/ticker-symbol', '/ticker-symbol')

  constructor() {}

  ngOnInit(): void {}

}
