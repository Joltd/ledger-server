import {Component, OnInit, ViewEncapsulation} from '@angular/core';
import {FilterExpression} from "../../model/load-config";
import {BrowserProvider, FilterField, RowField} from "../../model/browser-provider";

@Component({
  selector: 'browser-settings',
  templateUrl: './browser-settings.component.html',
  styleUrls: ['./browser-settings.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class BrowserSettingsComponent implements OnInit {

  browserProvider!: BrowserProvider
  columns: string[] = []

  filter: FilterExpression[] = []
  filterFields: FilterField[] = []
  rowFields: RowField[] = []

  constructor() {}

  ngOnInit(): void {}

  setup(browserProvider: BrowserProvider, columns: string[], filterExpression?: FilterExpression) {
    this.browserProvider = browserProvider
    this.columns = columns
    this.filter = filterExpression ? [filterExpression] : []
    this.filterFields = this.browserProvider.filterModel()
    this.rowFields = this.browserProvider.rowModel()
  }

  public filterExpression(): FilterExpression {
    return this.filter[0]
  }

}
