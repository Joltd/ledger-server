import {Component, Input, OnInit} from '@angular/core';
import {OverlayService} from "../../service/overlay.service";
import {OverlayCommand} from "../../model/overlay-command";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {FormBuilder, FormGroup} from "@angular/forms";
import {Observable} from "rxjs";
import {EditorProvider, EntityField} from "../../model/editor-provider";

@Component({
  selector: 'editor',
  templateUrl: './editor.component.html',
  styleUrls: ['./editor.component.scss']
})
export class EditorComponent implements OnInit {

  @Input()
  editorProvider!: EditorProvider

  model: EntityField[] = []
  form: FormGroup = new FormGroup({})

  constructor(
    private formBuilder: FormBuilder,
    private overlayService: OverlayService,
    private breakpointObserver: BreakpointObserver
  ) {}

  ngOnInit(): void {
    this.model = this.editorProvider.entityModel()
    this.setupCommands()
    this.initForm()
    this.load()
  }

  isHandset(): boolean {
    return this.breakpointObserver.isMatched(Breakpoints.Handset)
  }

  private setupCommands() {
    this.overlayService.setupCommands([
      new OverlayCommand('', 'save', () => this.apply()),
      new OverlayCommand('', 'close', () => this.close())
    ])
  }

  private load() {
    this.editorProvider.loadById()
      .subscribe(result => {
        this.fillForm(result)
      })
  }

  private apply() {
    let entity = this.fillEntity()
    this.editorProvider.update(entity)
      .subscribe(() => {
        this.close()
      })
  }

  private close() {
    this.editorProvider.done()
  }

  private initForm() {
    let formDefinition: any = {}
    this.model.map(field => {
        formDefinition[field.name] = [null]
      })
    this.form = this.formBuilder.group(formDefinition)
  }

  private fillForm(entity: any) {
    this.model.map(field => {
        this.form.get(field.name)?.setValue(entity[field.name])
      })
  }

  private fillEntity(): any {
    let entity: any = {}
    this.model.map(field => {
        entity[field.name] = this.form.get(field.name)?.value
      })
    return entity
  }

}
