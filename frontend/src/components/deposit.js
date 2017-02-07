import dialog from "./dialog";
import * as Api from "../api";

const deposit = (to) => () => {
    const inputs = '<input id="amount-input" placeholder="amount" type="number" step="any" class="swal2-input swal2-donatr-input">';
    dialog('Deposit!', inputs,
        resolve => (resolve([
            document.querySelector('#amount-input').value
        ])),
        () => document.querySelector('#amount-input').focus(),
        result => {
            Api.createDonation({to, value: result[0]});
            return "You deposited: " + result[0];
        }
    );
};

export default deposit