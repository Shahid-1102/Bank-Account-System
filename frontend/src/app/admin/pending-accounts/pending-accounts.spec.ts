import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PendingAccounts } from './pending-accounts';

describe('PendingAccounts', () => {
  let component: PendingAccounts;
  let fixture: ComponentFixture<PendingAccounts>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PendingAccounts]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PendingAccounts);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
