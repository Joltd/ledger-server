export class OverlayCommand {
  name!: string
  icon!: string
  executable: () => void = () => {}
  nested: boolean = false

  constructor(name: string, icon: string, executable: () => void) {
    this.name = name;
    this.icon = icon;
    this.executable = executable;
  }
}
