import { Component, OnInit } from '@angular/core';
import {Descriptor} from "../../../platform/model/descriptor";
import {environment} from "../../../environments/environment";

@Component({
  selector: 'ticker-symbol-editor',
  templateUrl: './ticker-symbol-editor.component.html',
  styleUrls: ['./ticker-symbol-editor.component.scss']
})
export class TickerSymbolEditorComponent implements OnInit {

  descriptor: Descriptor = new Descriptor('ticker.symbol.entity', environment.api + '/reference/ticker-symbol', '/ticker-symbol')

  constructor() {}

  ngOnInit(): void {}

}
