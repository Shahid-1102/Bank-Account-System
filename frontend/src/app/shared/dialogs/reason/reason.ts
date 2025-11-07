// Path: src/app/shared/dialogs/reason/reason.ts
import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

// --- Material Imports for Dialogs ---
import { MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';

// This interface defines the data we can pass TO the dialog
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
    FormsModule, // Use FormsModule for simple data binding
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
    this.dialogRef.close(); // Close the dialog without sending data
  }

  onConfirm(): void {
    // Close the dialog and send the reason text back
    this.dialogRef.close(this.reasonText); 
  }
}