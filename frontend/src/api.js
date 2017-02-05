import 'whatwg-fetch'

export const fetchDonaters = () =>
    fetch('/api/donaters')
        .then(response => response.json());

export const fetchDonatables = () =>
    fetch('/api/donatables')
        .then(response => response.json());

export const createDonater = ({name, email}) =>
    fetch('/api/donaters', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            name,
            email,
            balance: 0
        })
    });

export const createDonation = ({from, to, amount}) =>
    fetch('/api/donations', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({
            from,
            to,
            amount
        })
    });