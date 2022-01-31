import { Component, OnInit } from '@angular/core';
import {Turnover, TurnoverEntry} from "../../model/turnover";
import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {BehaviorSubject, Observable, of} from "rxjs";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {OverlayService} from "../../../platform/service/overlay.service";
import {OverlayCommand} from "../../../platform/model/overlay-command";
import {BrowserProvider, BrowserProviderImpl, FilterField, RowField} from "../../../platform/model/browser-provider";
import {FilterConfig, LoadConfig} from "../../../platform/model/load-config";
import {TurnoverReportSettingsComponent} from "../turnover-report-settings-component/turnover-report-settings.component";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {TypeUtils} from "../../../core/type-utils";

@Component({
  selector: 'app-turnover-report',
  templateUrl: './turnover-report.component.html',
  styleUrls: ['./turnover-report.component.scss']
})
export class TurnoverReportComponent implements OnInit {

  dataSource: TurnoverDataSource = new TurnoverDataSource()
  total: TurnoverEntry = new TurnoverEntry('')
  private fromDate!: string
  private toDate!: string
  private filter: FilterConfig = new FilterConfig()

  constructor(
    private breakpointObserver: BreakpointObserver,
    private overlayService: OverlayService,
    private http: HttpClient
  ) {}

  ngOnInit(): void {
    this.setupCommonCommands()
    this.loadData()
  }

  isHandset(): boolean {
    return this.breakpointObserver.isMatched(Breakpoints.Handset)
  }

  private loadData() {
    let body = {
      from: this.fromDate,
      to: this.toDate,
      filter: this.filter
    }
    this.http.post<Turnover>(environment.api + '/report/turnover', body, TypeUtils.of(Turnover))
      .subscribe(result => {
        this.dataSource.setData(result.entries)
        this.total = result.total
      })
  }

  private setupCommonCommands() {
    this.overlayService.setupCommands([
      new OverlayCommand('', 'settings', () => this.openSettings())
    ])
  }

  private setupSettingsCommand(component: TurnoverReportSettingsComponent) {
    this.overlayService.setupCommands([
      new OverlayCommand('', 'check', () => this.saveSettings(component)),
      new OverlayCommand('', 'close', () => this.closeSettings())
    ])
  }

  private openSettings() {
    this.overlayService.openSide(TurnoverReportSettingsComponent)
      .subscribe(component => {
        this.setupSettingsCommand(component)
        component.setup(this.fromDate, this.toDate, [
          new FilterField('account', 'OBJECT', '', 'com.evgenltd.ledgerserver.common.TurnoverReport.account'),
          new FilterField('person', 'OBJECT', '', 'com.evgenltd.ledgerserver.common.TurnoverReport.person'),
          new FilterField('currency', 'OBJECT', '', 'com.evgenltd.ledgerserver.common.TurnoverReport.currency'),
          new FilterField('expenseItem', 'OBJECT', '', 'com.evgenltd.ledgerserver.common.TurnoverReport.expenseItem'),
          new FilterField('incomeItem', 'OBJECT', '', 'com.evgenltd.ledgerserver.common.TurnoverReport.incomeItem'),
          new FilterField('tickerSymbol', 'OBJECT', '', 'com.evgenltd.ledgerserver.common.TurnoverReport.tickerSymbol')
        ], this.filter.expression)
      })
  }

  private saveSettings(component: TurnoverReportSettingsComponent) {
    this.filter.expression = component.filterExpression()
    this.fromDate = component.getFromDate()
    this.toDate = component.getToDate()
    this.closeSettings()
    this.loadData()
  }

  private closeSettings() {
    this.setupCommonCommands()
    this.overlayService.closeSide()
  }

}

export class TurnoverDataSource implements DataSource<TurnoverEntry> {
  private subject: BehaviorSubject<TurnoverEntry[]> = new BehaviorSubject<TurnoverEntry[]>([])

  setData(data: TurnoverEntry[]) {
    this.subject.next(data)
  }

  connect(collectionViewer: CollectionViewer): Observable<TurnoverEntry[]> {
    return this.subject.asObservable()
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.subject.complete()
  }
}
