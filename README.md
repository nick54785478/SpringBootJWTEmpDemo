# SpringBootJWTEmpDemo

SpringBootJWTEmpDemo 部門管理系統

*概述: 此Demo是為了以前所待某銀行的外匯資產管理系統專案所設計出來的，使用過濾器Filter和JwtToken搭配資料庫來進行部門權限控制。

*使用框架及技術: SpringBoot、 SpringSecurity、JwtToken、SpringDataJPA/Hibernate

*資料庫為 h2

*使用測試工具: Postman

*資料庫設計: 使用者訊息表user、使用者角色表role、功能描述檔func、使用者角色對應表 user_role 、角色權限表role_auth

*角色說明: root(最高權限)、admin(系統管理者)、dba(資料庫管理員)、supervisor(中階用戶(部門主管))、handler(經辦人員)、examiner(覆核人員)、user(一般用戶)
