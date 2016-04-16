'use strict';

const client = require('superagent');
const process = require('process');

let jwt;

const oldMeteUrl = process.argv[2];
const newMeteUrl = process.argv[3];

console.log('migrating from ' + oldMeteUrl + ' to ' + newMeteUrl);

client.post(newMeteUrl + '/api/session')
    .send({username: 'test', password: 'test'})
    .end((err, res) => {
        jwt = res.body;
        client.post(newMeteUrl + '/api/account')
            .set('Authorization', 'Bearer ' + jwt)
            .send({
                name: 'ye old mete',
                email: 'mete@fnord.fnet'
            })
            .end((err, res) => {
                const yeoldmeteId = res.body.id;
                client.get(oldMeteUrl + '/drinks.json')
                    .end((err, res) => {
                        const drinks = res.body;
                        drinks.map((drink) => {
                            client.get(oldMeteUrl + '/' + drink.logo_url)
                                .end((err, res) => {
                                    var base64Image = 'data:' + drink.logo_content_type + ';base64,' + new Buffer(res.body, 'binary').toString('base64');
                                    client.post(newMeteUrl + '/api/donatable')
                                        .set('Authorization', 'Bearer ' + jwt)
                                        .send({
                                            name: drink.name,
                                            amount: parseFloat(drink.price),
                                            imageUrl: base64Image
                                        }).end()
                                });
                        });
                        client.get(oldMeteUrl + '/users.json')
                            .end((err, res) => {
                                const users = res.body;
                                users.map((user) => {
                                    client.post(newMeteUrl + '/api/account')
                                        .set('Authorization', 'Bearer ' + jwt)
                                        .send({
                                            name: user.name,
                                            email: user.email
                                        })
                                        .end((err, res) => {
                                            client.post(newMeteUrl + '/api/transaction')
                                                .set('Authorization', 'Bearer ' + jwt)
                                                .send({
                                                    accountFrom: yeoldmeteId,
                                                    accountTo: res.body.id,
                                                    amount: parseFloat(user.balance)
                                                })
                                                .end()
                                        })
                                });
                            });
                    });
            });
    });

setTimeout(() => {
    client.post(newMeteUrl + '/api/session')
        .send({username: 'test', password: 'test'})
        .end((err, res) => {
            const jwt = res.body;
            [
                {amount: 5, logo: oldMeteUrl + '/assets/euro-5.png'},
                {amount: 10, logo: oldMeteUrl + '/assets/euro-10.png'},
                {amount: 20, logo: oldMeteUrl + '/assets/euro-20.png'},
                {amount: 50, logo: oldMeteUrl + '/assets/euro-50.png'}

            ].map((donatable) => {
                client.get(donatable.logo)
                    .end((err, res) => {
                        const base64Image = 'data:image/png;base64,' + new Buffer(res.body, 'binary').toString('base64');
                        client.post(newMeteUrl + '/api/donatable')
                            .set('Authorization', 'Bearer ' + jwt)
                            .send({
                                name: '+' + donatable.amount + '€',
                                amount: parseFloat(-donatable.amount),
                                imageUrl: base64Image
                            }).end()
                    });
            });
        });
}, 20000);