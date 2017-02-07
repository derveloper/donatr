import swal from "sweetalert2";
import * as Api from "../api";

const deposit = (to) => () => {
    swal({
        title: "Deposit!",
        text: "Type in the amount",
        input: "text",
        background: '#000',
        showCancelButton: true,
        animation: "slide-from-top",
        inputPlaceholder: "amount"
    }).then(function (inputValue) {
        if (inputValue === false) return false;

        if (inputValue === "") {
            swal.showInputError("You need to type in something!");
            return false
        }

        Api.createDonation({to, value: inputValue});

        swal({
            titleText: "Nice!",
            background: '#000',
            text: "You deposited: " + inputValue,
            type: "success"
        });
    });
};

export default deposit