import {Component, Input, OnInit, ViewEncapsulation} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Descriptor, DtoModel, FieldType} from "../../model/descriptor";
import {TypeUtils} from "../../../core/type-utils";
import {CollectionViewer, DataSource} from "@angular/cdk/collections";
import {BehaviorSubject, Observable} from "rxjs";
import {PageEvent} from "@angular/material/paginator";
import {LoadConfig} from "../../model/load-config";
import {DatePipe, DecimalPipe} from "@angular/common";
import {TranslocoService} from "@ngneat/transloco";
import {OverlayService} from "../../service/overlay.service";
import {BrowserSettingsComponent} from "../browser-settings/browser-settings.component";
import {OverlayCommand} from "../../model/overlay-command";
import {Router} from "@angular/router";

@Component({
  selector: 'browser',
  templateUrl: './browser.component.html',
  styleUrls: ['./browser.component.scss'],
  encapsulation: ViewEncapsulation.None
})
export class BrowserComponent implements OnInit {

  @Input()
  descriptor!: Descriptor

  private decimalPipe: DecimalPipe
  private datePipe: DatePipe

  dtoModel!: DtoModel
  dataSource: RemoteDateSource = new RemoteDateSource()
  config: LoadConfig = new LoadConfig()
  private columns: string[] = []

  constructor(
    private http: HttpClient,
    private router: Router,
    private translocoService: TranslocoService,
    private overlayService: OverlayService
  ) {
    let lang = this.translocoService.getActiveLang()
    this.decimalPipe = new DecimalPipe(lang)
    this.datePipe = new DatePipe(lang)
    this.setupCommonCommands()
  }

  ngOnInit(): void {
    this.config.page.size = 50
    this.loadDescriptor()
    this.loadData()
  }

  private loadDescriptor() {
    this.http.get<DtoModel>(this.descriptor.backend + '/descriptor/dto', TypeUtils.of(DtoModel))
      .subscribe(result => {
        this.dtoModel = result
        this.columns = this.dtoModel.fields.map(entry => entry.reference);
      })
  }

  private loadData() {
    this.http.post<number>(this.descriptor.backend + '/count', this.config)
      .subscribe(result => {
        this.config.page.length = result
        this.http.post<any[]>(this.descriptor.backend + '/', this.config)
          .subscribe(result => this.dataSource.setData(result))
      })
  }

  add() {
    this.router.navigate([this.descriptor.frontend + '/new']).then()
  }

  edit(id: number) {
    this.router.navigate([this.descriptor.frontend + '/', id]).then()
  }

  remove(id: number) {
    this.http.delete(this.descriptor.backend + '/' + id)
      .subscribe(() => this.loadData())
  }

  allowedColumns(): string[] {
    let columns = this.columns.slice();
    columns.push('action')
    return columns
  }

  format(value: string, type: FieldType, format: string): string {
    if (type == 'NUMBER') {
      return this.decimalPipe.transform(value, format)!
    } else if (type == 'DATE') {
      return this.datePipe.transform(value, format)!
    } else {
      return value
    }
  }

  onPageChanged(event: PageEvent) {
    this.config.page.index = event.pageIndex
    this.loadData()
  }

  onSortChanged(event: any) {
    if (event.direction) {
      this.config.sort.reference = event.active
      this.config.sort.order = event.direction.toUpperCase()
    } else {
      this.config.sort.reference = undefined
    }
    this.loadData()
  }

  // toolbar

  private setupCommonCommands() {
    this.overlayService.setupCommands([
      new OverlayCommand('', 'settings', () => this.openSettings())
    ])
  }

  private setupSettingsCommand(component: BrowserSettingsComponent) {
    this.overlayService.setupCommands([
      new OverlayCommand('', 'check', () => this.saveSettings(component)),
      new OverlayCommand('', 'close', () => this.closeSettings())
    ])
  }

  private openSettings() {
    this.overlayService.openSide(BrowserSettingsComponent)
      .subscribe(component => {
        this.setupSettingsCommand(component)
        component.setup(this.descriptor, this.dtoModel, this.columns, this.config.filter.expression)
      })
  }

  private saveSettings(component: BrowserSettingsComponent) {
    this.columns = component.columns
    this.config.filter.expression = component.filterExpression()
    this.closeSettings()
    this.loadData()
  }

  private closeSettings() {
    this.setupCommonCommands()
    this.overlayService.closeSide()
  }

}

export class RemoteDateSource implements DataSource<any> {

  private subject: BehaviorSubject<any[]> = new BehaviorSubject<any[]>([])

  setData(data: any[]) {
    this.subject.next(data)
  }

  connect(collectionViewer: CollectionViewer): Observable<any[]> {
    return this.subject.asObservable()
  }

  disconnect(collectionViewer: CollectionViewer): void {
    this.subject.complete()
  }

}
