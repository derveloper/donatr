import swal from "sweetalert2";

const dialog = (title, html, onResolve, onOpen, onResult) => {
    swal({
        title,
        html,
        background: '#000',
        preConfirm: function () {
            return new Promise(onResolve)
        },
        onOpen
    }).then(function (result) {
        swal({
            titleText: "Nice!",
            background: '#000',
            text: onResult(result),
            type: "success"
        });
    }).catch(swal.noop);
};

export default dialog;