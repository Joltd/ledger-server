import {Component, Input, OnInit} from '@angular/core';
import {EditorProvider, EntityField} from "../../../platform/model/editor-provider";
import {FormBuilder, FormGroup} from "@angular/forms";
import {OverlayService} from "../../../platform/service/overlay.service";
import {BreakpointObserver, Breakpoints} from "@angular/cdk/layout";
import {OverlayCommand} from "../../../platform/model/overlay-command";

@Component({
  selector: 'document-editor',
  templateUrl: './document-editor.component.html',
  styleUrls: ['./document-editor.component.scss']
})
export class DocumentEditorComponent implements OnInit {

  @Input()
  editorProvider!: EditorProvider

  private _model: EntityField[] = []
  private origin: any
  form: FormGroup = new FormGroup({})

  constructor(
    private formBuilder: FormBuilder,
    private overlayService: OverlayService,
    private breakpointObserver: BreakpointObserver
  ) {}

  ngOnInit(): void {
    this._model = this.editorProvider.entityModel()
    this.initForm()
    this.load()
    this.setupCommands()
  }

  isHandset(): boolean {
    return this.breakpointObserver.isMatched(Breakpoints.Handset)
  }

  endpoint(field: EntityField): string {
    return this.editorProvider.endpoint(field.typeName)
  }

  get model() {
    return this._model.filter(field => field.name != 'approved')
  }

  private setupCommands() {
    let commands = this.origin.approved
      ? [
        new OverlayCommand('', 'done', () => this.apply(this.origin.approved)),
        new OverlayCommand('', 'playlist_remove', () => this.apply(false)),
        new OverlayCommand('', 'close', () => this.close())
      ]
      : [
        new OverlayCommand('', 'done', () => this.apply(this.origin.approved)),
        new OverlayCommand('', 'playlist_add_check', () => this.apply(true)),
        new OverlayCommand('', 'close', () => this.close())
      ]

    this.overlayService.setupCommands(commands)
  }

  private load() {
    this.editorProvider.byId()
      .subscribe(result => {
        this.origin = result
        this.fillForm(result)
      })
  }

  private apply(approved: boolean) {
    let entity = this.fillEntity()
    entity.approved = approved
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
    this._model.map(field => {
      formDefinition[field.name] = [null]
    })
    this.form = this.formBuilder.group(formDefinition)
  }

  private fillForm(entity: any) {
    this._model.map(field => {
      this.form.get(field.name)?.setValue(entity[field.name])
    })
  }

  private fillEntity(): any {
    let entity: any = {}
    this._model.map(field => {
      entity[field.name] = this.form.get(field.name)?.value
    })
    return entity
  }

}
