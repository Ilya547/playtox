# Общее описание
Необходимо разработать приложение в соответствии с изложенными ниже требованиями.

## Общие требования
### Архитектура - 
Java SE 8.0 (или выше), использование библиотек и фреймворков на усмотрение исполнителя.
Должна быть система логирования (на основе готового решения, например Log4j). Приложение должно логировать в файл любые действия, приводящие к изменению данных. Приложение должно корректно обрабатывать и логировать ошибки.
### Структура данных
В приложении должна быть сущность Account (счет) содержащая поля:
ID (строковое) - идентификатор счета
Money (целочисленное) - сумма средств на счете.
### Функциональные требования
При запуске приложение должно создать четыре (или более) экземпляров объекта Account со случайными значениями ID и значениями money равным 10000.
В приложении запускается несколько (не менее двух) независимых потоков. Потоки должны просыпаться каждые 1000-2000 мс. Время на которое засыпает поток выбирается случайно при каждом исполнении.
Потоки должны выполнять перевод средств с одного счета на другой. Сумма списания или зачисления определяется случайным образом. Поле money не должно становиться отрицательным, сумма money на всех счетах не должна меняться.
Решение должно быть масштабируемым по количеству счетов и потоков и обеспечивать возможность одновременного (параллельного) перевода средств со счета a1 на счет a2 и со счета a3 на счет а4 в разных потоках.
Результаты всех транзакций должны записываться в лог.
После 30 выполненных транзакций приложение должно завершиться.

## Решение
Использовалось:
- Java 17
- Maven 4.0.0
- logback 1.2.11
- slf4 1.7.36

Предварительные параметры вносятся в конфигурационный файл src/main/resources/application.properties

threads.numbers - Стартовое количество потоков.

accounts.numbers - Количество аккаунтов.

transactions.numbers - Количество транзакций, после выполнения которых программа завершается.

Конфигурационный файл может быть расширен.

Логирование ведется в файл var/log/playtox.log

Настройки логирования доступны в файле src/main/resources/logback.xml

