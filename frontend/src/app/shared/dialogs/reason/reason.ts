import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// --- Material Imports for Dialogs ---
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

export interface ReasonDialogData {
  title: string;
  message: string;
  label: string;
}

@Component({
  selector: 'app-reason',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule, 
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ],
  templateUrl: './reason.html',
})
export class Reason {
  reasonText: string = '';

  constructor(
    public dialogRef: MatDialogRef<Reason>,
    @Inject(MAT_DIALOG_DATA) public data: ReasonDialogData
  ) {}

  onCancel(): void {
    this.dialogRef.close();
  }

  onConfirm(): void {
    this.dialogRef.close(this.reasonText); 
  }
}