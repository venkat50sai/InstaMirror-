import { Component, OnInit } from '@angular/core';
import { Authservice } from '../../service/authservice';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-products',
  imports: [FormsModule, CommonModule],
  templateUrl: './products.html',
  styleUrl: './products.scss',
})
export class Products implements OnInit {
  notificationMessage = '';
  notificationType: 'success' | 'error' | '' = '';
  notificationTimeoutId: any;

  products: any[] = [];
  selectedFile: File | null = null;

  editProductId: number | null = null;
  editProduct: any = {};
  editFile: File | null = null;

  newProduct = {
    name: '',
    desc:'',
    price: 0,
    stock: 0,
    image: '',
  };

  constructor(private authService: Authservice) {}

  ngOnInit() {
    this.loadProducts();
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  startEdit(product: any) {
    this.editProductId = product.id;
    this.editProduct = { ...product }; // clone
  }

  cancelEdit() {
    this.editProductId = null;
    this.editProduct = {};
    this.editFile = null;
  }

  onEditFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      this.editFile = file;
    }
  }

  updateProduct() {
    const formData = new FormData();

    formData.append('name', this.editProduct.name);
    formData.append('description', this.editProduct.description);
    formData.append('price', String(this.editProduct.price));
    formData.append('stock', String(this.editProduct.stock));

    if (this.editFile) {
      formData.append('img', this.editFile);
    }

    this.authService.updateProduct(this.editProduct.id, formData).subscribe({
      next: (res) => {
        this.setNotification(res.message, 'success');
        this.products = res.product;
        this.cancelEdit();
      },
      error: (err) => this.setNotification(err.error.message, 'error'),
    });
  }

  loadProducts() {
    this.authService.getProducts().subscribe({
      next: (res) => {
        this.products = res;
      },
      error: (err) => this.setNotification(err.error.message, 'error'),
    });
  }

  addProduct() {
    const headers = { Authorization: `Bearer ${localStorage.getItem('token')}` };

    const formData = new FormData();

    formData.append('name', this.newProduct.name);
    formData.append('description', this.newProduct.desc);
    formData.append('price', String(this.newProduct.price));
    formData.append('stock', String(this.newProduct.stock));

    if (this.selectedFile) {
      formData.append('img', this.selectedFile);
    }
    console.log(formData)
    
    this.authService.addProducts(formData).subscribe({
      next: (res) => {
        this.setNotification(res.message, 'success');
        this.products = res.product;
        this.resetForm();
      },
      error: (err) => this.setNotification(err.error.message, 'error'),
    });
  }

  increaseStock(product: any) {
    this.authService.updateProduct(product.id, product.stock + 1).subscribe({
      next: (res) => {
        this.setNotification(res.message, 'success');
        this.products = res.product;
      },
      error: (err) => this.setNotification(err.error.message, 'error'),
    });
  }

  decreaseStock(product: any) {
    this.authService.updateProduct(product.id, product.stock - 1).subscribe({
      next: (res) => {
        this.setNotification(res.message, 'success');
        this.products = res.product;
      },
      error: (err) => this.setNotification(err.error.message, 'error'),
    });
  }

  deleteProduct(id: any) {
    this.authService.deleteProduct(id).subscribe({
      next: (res) => {
        this.setNotification(res.message, 'success');
        this.products = res.product;
      },
      error: (err) => this.setNotification(err.error.message, 'error'),
    });
  }

  resetForm() {
    this.newProduct = {
      name: '',
      desc: '',
      price: 0,
      stock: 0,
      image: '',
    };

    this.selectedFile = null;
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
