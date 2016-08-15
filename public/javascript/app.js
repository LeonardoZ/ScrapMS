$(function(){

    var mainPageModule =  function(){
        var $tableManga = $('.table-manga');
        var url = '/subscribe';
        $tableManga.on('click', 'button.sub', subscribeOnClick);

        function subscribeOnClick(evt) {
            var $btnSub = $(evt.currentTarget);
            $btnSub.text("Loading...")
            var mangaId = $btnSub.data('manga-id');
            var data = {'mangaId' : mangaId.toString() };
            $.ajax({
                  type: 'POST',
                  url: url,
                  dataType : "json",
                  cache: false,
                  contentType: 'application/json',
                  data: JSON.stringify(data),
                  success: function(data) {
                    console.log(data);
                    $btnSub.text("Subscribed!")
                    $btnSub.toggleClass("sub!")
                    $btnSub.prop("disabled","disabled")
                  }
            });
        }

    };

    mainPageModule();


});