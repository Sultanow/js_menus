import { Component, OnInit, Input, OnChanges, ViewChild, ChangeDetectorRef, AfterViewInit } from '@angular/core';
import { NewsService } from '../service/news.service';
import { NewsItem } from '../model/news-item';
import { MatTableDataSource } from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatDialog } from '@angular/material/dialog';
import { DialogNewsComponent } from '../dialog-news/dialog-news.component';
import { DialogDeleteNewsComponent } from '../dialog-delete-news/dialog-delete-news.component';

@Component({
  selector: 'app-news',
  templateUrl: './news.component.html',
  styleUrls: [
    './news.component.css'
  ]
})
export class NewsComponent implements OnInit, OnChanges, AfterViewInit {
  @Input() showNews: boolean;
  @Input() isLoggedIn: boolean = true;

  @ViewChild(MatPaginator, { static: false }) paginator: MatPaginator;
  @ViewChild(MatSort, { static: false }) sort: MatSort;

  allNewsItems: NewsItem[];
  dataSource: MatTableDataSource<NewsItem> = new MatTableDataSource<NewsItem>();
  displayedColumns: string[] = [ 'id', 'title', 'text', 'date', 'priority', 'visible', 'actions' ];
  isAdminMode: boolean = false;

  lastSearchValue: string = "";

  constructor (
    private service: NewsService,
    private changeDetector: ChangeDetectorRef,
    private dialog: MatDialog
  ) { }

  ngOnInit(): void {

  }

  ngOnChanges(changes) {
    if (this.showNews) {
      this.updateDataSource();
    }
  }

  updateDataSource() {
    if (this.isAdminMode) {
        this.service.getAllNews().subscribe(data => {
          console.log(data);
          this.initializeTable(data);
        });
      } else {
        this.service.getAllVisibleNews().subscribe(data => {
          console.log(data);
          this.allNewsItems = data.sort((a, b)=> a.id > b.id ? 1 : a.id < b.id ? -1 : 1);
        });
      }
  }

  ngAfterViewInit(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  initializeTable(data: NewsItem[]) {
    this.allNewsItems = data;
    this.dataSource.data = data;
    this.changeDetector.detectChanges();
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  applyFilter(event: Event) {
    console.log(event);
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();
    console.log(this.dataSource);
    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
    this.changeDetector.detectChanges();
  }

  editItem($event, row) {
    console.log($event);
    console.log(row);
    this.openDialog(row);
  }

  openDialog(newsItem?: NewsItem): void {
    const dialogRef = this.dialog.open(DialogNewsComponent, {
      width: '30rem',
      height: '30rem',
      data: newsItem
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      console.log(result);
      if(newsItem && result) {
        this.service.updateNews(result).subscribe(result => {
          this.updateDataSource();
        })
      }
    });
  }

  deleteItem($event, row) {
    const dialogRef = this.dialog.open(DialogDeleteNewsComponent
    );

    dialogRef.afterClosed().subscribe(result => {
      if(result) {
        this.service.deleteNews(row).subscribe(result => {
          this.updateDataSource();
        })
      }
    });
  }

  addNewItem($event) {
    let item: NewsItem = new NewsItem();
    item.visible = true;
    const dialogRef = this.dialog.open(DialogNewsComponent, {
      width: '30rem',
      height: '30rem',
      data: item
    });

    dialogRef.afterClosed().subscribe(result => {
      console.log('The dialog was closed');
      console.log(result);
      if(result) {
        this.service.createNews(result).subscribe(result => {
          this.updateDataSource();
        })
      }
    });
  }

  applySearchFilter(val: string) {
    if(val.length >= 3) {
      this.lastSearchValue = val;
      this.service.findNewsByTag(val).subscribe(result => {
        this.allNewsItems = result;
        console.log(result);
      })
    } else if(this.lastSearchValue !== "" && val.length === 0) {
      this.lastSearchValue = "";
      this.updateDataSource();
    }
    console.log(val);
  }

  changeAdminMode() {
    console.log(this.isAdminMode);
    this.updateDataSource();
  }
}
