import {Component, OnInit, inject} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {CommonModule} from '@angular/common';
import {Beneficio, TransferenciaRequest} from './beneficio.model';
import {BeneficioService} from './beneficio.service';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './app.component.html' // 🔥 CORREÇÃO: separado o HTML
})
export class AppComponent implements OnInit {

    private readonly service = inject(BeneficioService);

    beneficios: Beneficio[] = [];

    form: Beneficio = this.emptyForm();

    transferForm: Partial<TransferenciaRequest> = {};

    ngOnInit(): void {
        this.load();
    }

    /**
     * 🔎 Carrega lista de benefícios
     */
    load(): void {
        this.service.list().subscribe(items => this.beneficios = items);
    }

    /**
     * 💾 Salva ou atualiza benefício
     */
    save(): void {
        const request = this.form.id
            ? this.service.update(this.form.id, this.form)
            : this.service.create(this.form);

        request.subscribe({
            next: () => {
                this.resetForm();
                this.load();
            },
            error: err => alert(this.extractMessage(err))
        });
    }

    /**
     * 🔄 Realiza transferência entre benefícios
     */
    transfer(): void {
        this.service.transfer(this.transferForm as TransferenciaRequest).subscribe({
            next: () => {
                alert('Transferência realizada com sucesso.');
                this.transferForm = {};
                this.load();
            },
            error: err => alert(this.extractMessage(err))
        });
    }

    /**
     * ✏️ Preenche formulário para edição
     */
    edit(item: Beneficio): void {
        this.form = {...item};
    }

    /**
     * 🗑️ Remove benefício
     */
    remove(id: number): void {
        this.service.delete(id).subscribe({
            next: () => this.load(),
            error: err => alert(this.extractMessage(err))
        });
    }

    /**
     * 🔄 Limpa formulário
     */
    resetForm(): void {
        this.form = this.emptyForm();
    }

    /**
     * 🔧 Cria objeto padrão
     */
    private emptyForm(): Beneficio {
        return {nome: '', descricao: '', valor: 0, ativo: true};
    }

    /**
     * ⚠️ Extrai mensagem de erro do backend
     */
    private extractMessage(err: any): string {
        return err?.error?.message ?? 'Falha ao processar requisição.';
    }
}