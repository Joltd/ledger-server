import {Component, Input, OnInit} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {OverlayService} from "../../service/overlay.service";
import {OverlayCommand} from "../../model/overlay-command";
import {ActivatedRoute, Router} from "@angular/router";
import {EditorDescriptor, MetaModel} from "../../model/descriptor";
import {TypeUtils} from "../../../core/type-utils";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {FormBuilder, FormGroup} from "@angular/forms";
import {environment} from "../../../environments/environment";
import {Observable} from "rxjs";

@Component({
  selector: 'editor',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.scss']
})
export class EditorComponent implements OnInit {

  @Input()
  descriptor!: EditorDescriptor
  private id!: number
  model!: MetaModel
  form: FormGroup = new FormGroup({})

  constructor(
    private http: HttpClient,
    private router: Router,
    private route: ActivatedRoute,
    private formBuilder: FormBuilder,
    private overlayService: OverlayService,
    private breakpointObserver: BreakpointObserver
  ) {}

  ngOnInit(): void {
    this.loadDescriptor()
      .subscribe(result => {
        this.model = result
        this.setupCommands()
        this.initForm()
        this.route.params.subscribe(params => {
          this.id = +params['id']
          this.load()
        })
      })
  }

  isHandset(): boolean {
    return this.breakpointObserver.isMatched(Breakpoints.Handset)
  }

  personApi(): string {
    return environment.api + '/order/person'
  }

  private setupCommands() {
    this.overlayService.setupCommands([
      new OverlayCommand('', 'save', () => this.apply()),
      new OverlayCommand('', 'close', () => this.close())
    ])
  }

  private loadDescriptor(): Observable<MetaModel> {
    return this.http.get<MetaModel>(this.descriptor.metaModel(), TypeUtils.of(MetaModel))
  }

  private load() {
    if (this.id) {
      this.http.get<any>(this.descriptor.read(this.id))
        .subscribe(result => {
          this.fillForm(result)
        })
    }
  }

  private apply() {
    let entity = this.fillEntity()
    this.http.post(this.descriptor.update(), entity)
      .subscribe(() => {
        this.close()
      })
  }

  private close() {
    this.router.navigate([this.descriptor.backTo()]).then()
  }

  private initForm() {
    let formDefinition: any = {}
    this.model?.fields
      .map(field => {
        formDefinition[field.reference] = [null]
      })
    this.form = this.formBuilder.group(formDefinition)
  }

  private fillForm(entity: any) {
    this.model?.fields
      .map(field => {
        this.form.get(field.reference)?.setValue(entity[field.reference])
      })
  }

  private fillEntity(): any {
    let entity: any = {}
    this.model.fields
      .map(field => {
        entity[field.reference] = this.form.get(field.reference)?.value
      })
    return entity
  }

}
