// java reflection of this file is located at
// demo-edit-es-module/src/main/java/org/sudu/experiments/EditJsApi.java

import { Channel, IDisposable } from "./common";

export {
    Channel, Message, setLogLevel, setLogOutput, newRemoteChannelTest, LogLevel, ChannelTestApi, IDisposable
} from './common';

export interface EditArgs {
    containerId: string

    // default value for workerUrl is "worker.js"
    workerUrl?: string | URL

    theme?: Theme

    readonly?: boolean

    // number of worker threads for parsing and resolve
    // default: 2
    numThreads?: number

    codiconUrl?: string
}

interface Uri {
    scheme?: string,
    authority?: string,
    path?: string
}

interface IEvent<T> {
    /**
     * Registers disposable event callback
     *
     * When event is fired, a new value V will be delivered to all listeners that were registered at the moment when the value V processing started.
     * It means that if listener A disposes listener B during processing of value V, listener B will still receive value V.
     * @param listener callback to be called when event is fired
     */
    (listener: (e: T) => any): IDisposable;
}

/**
 * An event describing that an editor has had its model reset (i.e. `editor.setModel()`).
 */
export interface IModelChangedEvent {
    /**
     * The `uri` of the previous model or null.
     */
    readonly oldModelUrl: Uri | null;
    /**
     * The `uri` of the new model or null.
     */
    readonly newModelUrl: Uri | null;
}

export interface ITextModel extends IDisposable {
    language?: string
    uri?: Uri
    getOffsetAt(position: IPosition): number
    getPositionAt(offset: number): IPosition
    getText(): string
}

type ProviderValue<T> = T | undefined | null;
type ProviderResult<T> = ProviderValue<T> | Promise<ProviderValue<T>>;

// Monaco like IPosition line and column starts with 1:
//      1st line has number 1,
//      first char column is 1, 1st glyph is between column 1 and column 2
interface IPosition {
    column: number,
    lineNumber: number
}

// numeration starts from 1
interface IRange {
    endColumn: number,
    endLineNumber: number,
    startColumn: number,
    startLineNumber: number
}

interface ICancellationToken {
    isCancellationRequested: boolean
}

interface ILocation {
    range: IRange,
    uri: Uri
}

export enum DocumentHighlightKind {
    Text = 0,
    Read = 1,
    Write = 2
}

interface IDocumentHighlight {
    range: IRange,
    kind?: DocumentHighlightKind
}

interface IDefinitionProvider {
    provideDefinition(
        model: ITextModel,
        position: IPosition,
        token: ICancellationToken
    ): ProviderResult<ILocation[]>
}

interface IDeclarationProvider {
    provideDeclaration(
        model: ITextModel,
        position: IPosition,
        token: ICancellationToken
    ): ProviderResult<ILocation[]>
}

interface IReferenceProvider {
    provideReferences(
        model: ITextModel,
        position: IPosition,
        context: { includeDeclaration: boolean },
        token: ICancellationToken
    ): ProviderResult<ILocation[]>
}

interface IDocumentHighlightProvider {
    provideDocumentHighlights(
        model: ITextModel,
        position: IPosition,
        token: ICancellationToken
    ): ProviderResult<IDocumentHighlight[]>
}

type SelectionOrPosition = IRange | IPosition;

interface ICodeEditorOpener {
    openCodeEditor(
        source: ICodeEditor,
        resource: Uri,
        selectionOrPosition?: SelectionOrPosition
    ): boolean | Promise<boolean>;
}

interface ILanguageFilter {
    language?: string
    scheme?: string
}

type LanguageSelector = string | ILanguageFilter | Array<string | ILanguageFilter>;

// function apply(filter:LanguageSelector, model: ITextModel) : boolean {
//    if (Array.isArray(filter)) foreach...
//    else return apply(filter)
// }

// function apply(filter:LanguageFilter, model: ITextModel) : boolean {
//     if (filter.language && model.language && filter.language !== model.language) return false;
//     if (filter.scheme && model.uri.scheme && filter.scheme !== model.uri.scheme) return false;
//     return true;
// }

export const enum ThemeColor {
    TreeViewBackground = 0,
    DefaultForeground = 1,
    SelectedItemBackground = 2,
    SelectedItemForeground = 3,
    HoveredItemBackground = 4,
    InactiveSelectionBackground = 5,
    ChangedItemBackground = 6
}

export type BaseTheme = 'dark' | 'light'

export type Theme = {
    [color in ThemeColor]?: string;
} & {
    baseTheme: BaseTheme;
} | BaseTheme;

interface HasTheme {
    setFontFamily(fontFamily: string): void,

    setFontSize(fontSize: number): void,

    setTheme(theme: Theme): void
}

interface Focusable {
    focus(): void
}

interface EditorBase {
    disconnectFromDom(): void
    reconnectToDom(containerId?: string): void
}

export interface TwoPanelDiff {
    setReadonly(rightReadonly: boolean, leftReadonly: boolean): void
}

export interface ICodeDiff extends EditorBase, HasTheme, Focusable, TwoPanelDiff {
    setLeftModel(model: ITextModel): void,

    setRightModel(model: ITextModel): void,

    getLeftModel(): ITextModel,

    getRightModel(): ITextModel,
}

interface CodeDiffView extends ICodeDiff, IDisposable {
}

export interface ICodeEditor extends EditorBase, HasTheme, Focusable {
    setReadonly(flag: boolean): void,

    setText(text: string): void,

    getText(): string,

    setModel(model: ITextModel): void,

    setPosition(position: IPosition): void

    getPosition(): IPosition

    getModel(): ITextModel,

    registerDefinitionProvider(languageSelector: LanguageSelector, provider: IDefinitionProvider): IDisposable,
    registerDeclarationProvider(languageSelector: LanguageSelector, provider: IDeclarationProvider): IDisposable;
    registerReferenceProvider(languageSelector: LanguageSelector, provider: IReferenceProvider): IDisposable,

    registerDocumentHighlightProvider(languageSelector: LanguageSelector, provider: IDocumentHighlightProvider): IDisposable,

    registerEditorOpener(opener: ICodeEditorOpener): IDisposable

    revealLineInCenter(line: number): void

    revealLine(line: number): void

    revealPosition(position: IPosition): void

    onDidChangeModel: IEvent<IModelChangedEvent>
}

interface EditView extends ICodeEditor, IDisposable {
}

export interface IFolderDiff extends EditorBase, HasTheme, TwoPanelDiff, Focusable {
    isReady(): boolean
    onReadyChanged: IEvent<boolean>
}

export interface FolderDiffView extends IFolderDiff, IDisposable {
}

export interface FolderDiffSelection {
    // relativePath does not include root folder.
    // For root folder relativePath is empty
    relativePath: string
    isLeft: boolean
    isFolder: boolean

    // whether selected element exists only in current diff side
    isOrphan: boolean
}

export interface FileDiffSelection {
}

export interface DiffViewController {
    getViewType(): 'folderDiff' | 'fileDiff'
    getSelection(): FolderDiffSelection | FileDiffSelection | undefined

    canNavigateUp(): boolean
    navigateUp(): void

    canNavigateDown(): boolean
    navigateDown(): void
}

export enum DiffType {
    Same = 0, Added = 1, Deleted = 2, Modified = 3
}

export interface FolderDiffViewController extends DiffViewController {
    getViewType(): 'folderDiff'
    getSelection(): FolderDiffSelection | undefined

    getDiffFilter(): DiffType[]
    applyDiffFilter(filters: DiffType[]): void
}

export interface FileDiffViewController extends DiffViewController {
    getViewType(): 'fileDiff'
    getSelection(): FileDiffSelection | undefined
}

export interface RemoteFolderDiffView extends FolderDiffView {
    getState(): any
    applyState(state: any): void
    getController(): DiffViewController;
    onControllerUpdate: IEvent<FolderDiffViewController | FileDiffViewController>
}

export interface RemoteCodeDiffView extends CodeDiffView {
    getState(): any
    applyState(state: any): void
    getController(): FileDiffViewController;
    onControllerUpdate: IEvent<FileDiffViewController>
}

export function newTextModel(text: string, language?: string, uri?: Uri): ITextModel

export function newEditor(args: EditArgs): Promise<EditView>

export function newCodeDiff(args: EditArgs): Promise<CodeDiffView>

export function newFolderDiff(args: EditArgs): Promise<FolderDiffView>

export function newRemoteFolderDiff(args: EditArgs, channel: Channel): Promise<RemoteFolderDiffView>

export function newRemoteCodeDiff(args: EditArgs, channel: Channel): Promise<RemoteCodeDiffView>
