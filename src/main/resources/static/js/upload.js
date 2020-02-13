document.addEventListener('DOMContentLoaded', function () {
    Dropzone.options.imageUpload = {
        maxFileSize: 2,
        acceptedFiles: 'image/*',
        success: async function (file, response){
            const result = await fetch('/me/images');
            const html = await result.text();

            $('.album-list').replaceWith($(html).find('.album-list'));
        }
    };
});
