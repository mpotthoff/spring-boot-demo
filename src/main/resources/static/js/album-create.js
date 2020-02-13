function reindexImages() {
    const $albumImages = $('#album-images');

    let index = 0;
    for (const albumImage of $albumImages.children()) {
        const $albumImage = $(albumImage);

        const $id = $($albumImage.find('.id')[0]);
        const $description = $($albumImage.find('.description')[0]);

        $id.attr('name', `images[${index}].id`);
        $id.attr('id', `images${index}.id`);
        $description.attr('name', `images[${index}].description`);
        $description.attr('id', `images${index}.description`);

        index++;
    }
}

function addImage(id) {
    const $albumImages = $('#album-images');

    const html = `
<div class="album-image container border mt-2 mb-2">
    <div class="row">
        <div class="col-lg-3 col-md-4 col-sm-5 mt-sm-0 mb-sm-0 mt-2 mb-2 align-self-center">
            <div class="btn-toolbar justify-content-center">
                <button class="btn btn-outline-primary image-cover-image" value="${id}"><i class="fas fa-image"></i></button>

                <div class="btn-group ml-2 mr-2">
                    <button class="btn btn-secondary image-move-up"><i class="fas fa-chevron-up"></i></button>
                    <button class="btn btn-secondary image-move-down"><i class="fas fa-chevron-down"></i></button>
                </div>

                <button class="btn btn-danger image-remove"><i class="fas fa-trash-alt"></i></button>
            </div>
        </div>
        <div class="col-lg-4 col-md-4 col-sm-4 align-self-center text-center">
            <textarea class="form-control description" name="description" rows="3" maxlength="255" style="resize: none;" id="imagesXXX.description" name="images[XXX.]description"></textarea>
        </div>
        <div class="col-lg-5 col-md-4 col-sm-3 align-self-center text-center">
            <input type="hidden" name="images[XXX].id" value="${id}" id="imagesXXX.id" class="id">
            <img src="/i/${id}.png" class="mt-2 mb-2" style="max-height: 9em; max-width: 100%;" />
        </div>
    </div>
</div>
    `;

    $albumImages.append($(html));
    reindexImages();
}

function moveUp(image) {
    const $image = $(image);

    $image.prev().insertAfter($image);

    reindexImages();
}

function moveDown(image) {
    const $image = $(image);

    $image.next().insertBefore($image);

    reindexImages();
}

function removeImage(image) {
    const $image = $(image);

    $image.remove();

    reindexImages();
}

document.addEventListener('DOMContentLoaded', function () {
    const $modal = $('#images-modal');
    const $modalBody = $('#images-modal-body');
    const $modalSave = $('#images-modal-save');
    const $albumImages = $('#album-images');
    const $coverImage = $('#input-cover-image');

    $modal.on('shown.bs.modal', async function () {
        try {
            $modalBody.html('Loading...');

            const response = await fetch('/me/images.json');
            const images = await response.json();

            if (images.length < 1) {
                $modalBody.html('No Images!');
                return;
            }

            let html = '';

            for (const image of images) {
                html += `
<div class="row mb-2">
    <div class="col-md-1 align-middle">
        <div class="form-check form-check-inline" style="height: 100%;">
          <input class="form-check-input image-select-checkbox" type="checkbox" value="${image.id}">
        </div>
    </div>
    <div class="col-md-11 ml-auto align-middle">
        <img src="/i/${image.id}.png" style="max-width: 100%;" />
    </div>
</div>
                `;
            }

            $modalBody.html(html);
        } catch (error) {
            $modalBody.html('An error occurred!');
        }
    });

    $modal.on('hidden.bs.modal', function () {
        $modalBody.html('');
    });

    $modalSave.on('click', function () {
        const checkboxes = $('.image-select-checkbox');

        for (const checkbox of checkboxes) {
            if (checkbox.checked) {
                addImage(checkbox.value);
            }
        }

        $modal.modal('hide');
    });

    $albumImages.on('click', '.image-move-up', function (event) {
        event.preventDefault();

        moveUp($(this).closest('.album-image')[0]);
    });

    $albumImages.on('click', '.image-move-down', function (event) {
        event.preventDefault();

        moveDown($(this).closest('.album-image')[0]);
    });

    $albumImages.on('click', '.image-remove', function (event) {
        event.preventDefault();

        removeImage($(this).closest('.album-image')[0]);
    });

    $albumImages.on('click', '.image-cover-image', function (event) {
        event.preventDefault();

        const $this = $(this);

        $('.image-cover-image').removeClass('btn-primary').addClass('btn-outline-primary');
        $this.removeClass('btn-outline-primary').addClass('btn-primary');

        $coverImage.val($this.val());
    });

    const tags = new Bloodhound({
        datumTokenizer: Bloodhound.tokenizers.obj.whitespace('tag'),
        queryTokenizer: Bloodhound.tokenizers.whitespace,
        prefetch: {
            url: '/t.json',
            filter: function (list) {
                return list.map(function(tag) {
                    return {
                        tag: tag
                    };
                });
            }
        }
    });
    tags.initialize();

    $('#image-tags').tagsinput({
        typeaheadjs: {
            name: 'tags',
            displayKey: 'tag',
            valueKey: 'tag',
            source: tags.ttAdapter()
        }
    });
});
