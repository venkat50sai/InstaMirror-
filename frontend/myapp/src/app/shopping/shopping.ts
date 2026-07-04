import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Authservice } from '../service/authservice';
import { Router } from '@angular/router';

@Component({
  selector: 'app-shopping',
  imports: [CommonModule],
  templateUrl: './shopping.html',
  styleUrl: './shopping.scss',
})
export class Shopping implements OnInit {
  constructor(
    private authService: Authservice,
    private router: Router,
  ) {}

  notificationMessage = '';
  notificationType: 'success' | 'error' | '' = '';
  notificationTimeoutId: any;

  cartCount = 0;
  cart: any = {};
  products: any = [];

  ngOnInit(): void {
    this.authService.getProducts().subscribe({
      next: (data) => (this.products = data),
      error: (err) => {
        this.setNotification(err.error.message, 'error');
      },
    });
    this.loadCart();
  }

  loadCart() {
    this.authService.getCart().subscribe({
      next: (data) => {
        this.cart = data;
        this.cartCount = this.getTotalCount(data);
      },
      error: (err) => {
        this.setNotification(err.error.message, 'error');
      },
    });
  }

  getTotalCount(cart: any): number {
    if (!cart?.items) return 0;
    return cart.items.reduce((sum: number, item: any) => sum + item.quantity, 0);
  }

  addToCart(product: any) {
    this.authService.addToCart(1, product.id).subscribe({
      next: (res) => {
        this.setNotification(res.message, 'success');
        this.cart = res.cart;
        this.cartCount = res.count;
      },
      error: (err) => this.setNotification(err.error.message, 'error'),
    });
  }

  getProductQty(productId: number): number {
    if (!this.cart || !Array.isArray(this.cart.items)) return 0;

    const item = this.cart.items.find((c: any) => c.productId === productId);

    return item ? item.quantity : 0;
  }

  increaseQty(product: any) {
    const currentQty = this.getProductQty(product.id);

    this.authService.updateCart(currentQty + 1, product.id).subscribe({
      next: (res) => {
        this.setNotification(res.message, 'success');
        this.cart = res.cart;
        this.cartCount = res.count;
      },
      error: (err) => this.setNotification(err.error.message, 'error'),
    });
  }

  decreaseQty(product: any) {
    const currentQty = this.getProductQty(product.id);

    if (currentQty <= 1) {
      this.authService.removeItem(product.id).subscribe({
        next: (res) => {
          this.setNotification(res.message, 'success');
          this.cart = res.cart;
          this.cartCount = res.count;
        },
        error: (err) => this.setNotification(err.error.message, 'error'),
      });
    } else {
      this.authService.updateCart(currentQty - 1, product.id).subscribe({
        next: (res) => {
          this.setNotification(res.message, 'success');
          this.cart = res.cart;
          this.cartCount = res.count;
        },
        error: (err) => this.setNotification(err.error.message, 'error'),
      });
    }
  }

  viewCart() {
    this.router.navigate(['/payment']);
  }

  setNotification(message: string, type: 'success' | 'error') {
    this.notificationMessage = message;
    this.notificationType = type;

    if (this.notificationTimeoutId) {
      clearTimeout(this.notificationTimeoutId);
    }

    this.notificationTimeoutId = setTimeout(() => {
      this.clearNotification();
    }, 4000);
  }

  clearNotification() {
    this.notificationMessage = '';
    this.notificationType = '';
    if (this.notificationTimeoutId) {
      clearTimeout(this.notificationTimeoutId);
      this.notificationTimeoutId = null;
    }
  }
}
