# CsvCRUD
=====================
Необходимо реализовать консольное CRUD приложение, которое имеет следующие сущности:

*`Customer`
*`Specialty`
*`Account`
*`AccountStatus (enum ACTIVE, BANNED, DELETED)`

В качестве хранилища данных необходимо использовать CSV файлы:

customers.csv, specialties.csv, accounts.csv

Пользователь в консоли должен иметь возможность создания, получения, редактирования и удаления данных.

Слои:
-------------
model - POJO классы

'''repository - классы, реализующие доступ к текстовым файлам
controller - обработка запросов от пользователя
view - все данные, необходимые для работы с консолью'''

Например: Customer, CustomerRepository, CustomerController, CustomerView и т.д.

Для репозиторного слоя желательно использовать базовый интерфейс:
'interface GenericRepository<T,ID>'

'Interface CustomerRepository extends GenericRepository<Customer, Long>'

'class CsvCustomerRepositoryImpl implements CustomerRepository'

В рамках данного проекта необходимо активно использовать возможности Stream API и шаблоны проектирования.

Результатом выполнения задания должен быть отдельный слой приложения, реализованного в рамках модуля  Java IO/NIO под названием csv. 

Слой controller должен использовать csv реализацию.

---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Create Read Update Delete:
============================ 
Working with Customers:
----------------------
                * to create a new account: -c, firstName, lastName, age, accountStatus, specialty1, specialty2...
                * to read all accounts: -r
                * to read the account by id: -r, id
                * to update the account: -u, id, firstName, lastName, age, accountStatus, specialty1, specialty2... 
                * to delete the account: -d, firstName, lastName, age, accountStatus, specialty1, specialty2...
                * to delete account by id: -d, id
Working with Accounts:
---------------------
                * to create a new account: -c, firstName, lastName, age, accountStatus
                * to read all accounts: -r
                * to read the account by id: -r, id
                * to update the account: -u, id, firstName, lastName, age, accountStatus
                * to delete the account: -d, firstName, lastName, age, accountStatus
                * to delete account by id: -d, id
Working with Specialties:
--------------------
                * to create a new account: -c, name
                * to read all accounts: -r
                * to read the account by id: -r, id
                * to update the account: -u, id, name
                * to delete the account: -d, name
                * to delete account by id: -d, id
