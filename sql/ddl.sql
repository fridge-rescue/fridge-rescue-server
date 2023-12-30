
# Create member Table
create table member
(
    member_id   bigint unique      not null auto_increment,
    fridge_id   bigint             not null,
    name        varchar(15)        not null,
    nickname    varchar(15)        not null,
    email       varchar(50) unique not null,
    phone       varchar(11),
    password    varchar(100)       not null,
    role        enum ('USER','ADMIN') not null,
    provider    enum('EMAIL','GOOGLE') not null,
    provider_id varchar(100),
    email_code  varchar(10),
    jwt_token   varchar(255),
    created_at  datetime(6) not null,
    modified_at datetime(6),

    primary key (member_id)
);

# Create fridge Table
create table fridge
(
    fridge_id bigint unique not null auto_increment,
    member_id bigint not null,
    created_at datetime(6) not null,
    modified_at datetime(6),

    primary key (fridge_id),
    foreign key (member_id) references member (member_id)
);

# Create notification Table
create table notification
(
    notification_id   bigint unique not null auto_increment,
    member_id         bigint        not null,
    notification_type enum ('INGREDIENT_EXPIRED','RECIPE_REVIEWED','RECIPE_RECOMMENDED'),
    notification_text json          not null,
    created_at        datetime(6) not null,
    checked_at        datetime(6) null,

    primary key (notification_id),
    foreign key (member_id) references member (member_id)
);

# 레시피 후기 테이블 생성
create table review
(
    review_id   bigint unique not null auto_increment,
    member_id   bigint        not null,
    #           recipe_id bigint not null,
    cook_id     bigint        not null,
    title       varchar(50)   not null,
    image_url   varchar(100),
    contents    varchar(1000) not null,
    created_at  datetime(6) not null,
    modified_at datetime(6),

    primary key (review_id),
    foreign key (member_id) references member (member_id) # foreign key (recipe_id) references recipe (recipe_id)
);

# Create cook Table
create table cook
(
    cook_id    bigint unique not null auto_increment,
    member_id  bigint        not null,
    #          recipe_id bigint not null,
    created_at datetime(6) not null,

    primary key (cook_id),
    foreign key (member_id) references member (member_id) # foreign key (recipe_id) references recipe (recipe_id)
)
